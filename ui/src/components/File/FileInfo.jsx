import { HStack, VStack, Text, Button, useDisclosure } from "@chakra-ui/react";
import FileAttributes from "../Folder/FolderContents/FileRow/FileInfoButton/FileAttributes";
import { useQuery } from "@tanstack/react-query";
import { useAuthContext } from "../../context/AuthContext";
import PermissionsList from "../Folder/FolderInfoButton/PermissionsList";
import { apiInstance } from "../../lib/axios";
import FileViewer from "../Folder/FolderContents/FileRow/FileViewer";
import CopyLinkButton from "../Folder/FolderInfoButton/CopyLinkButton";

const FileInfo = ({ file }) => {
	const { user } = useAuthContext();
	const isUserOwner = user.id === file.owner.id;

	const {
		data: permissions,
		isSuccess,
		isLoading,
		isError,
	} = useQuery({
		queryKey: ["file", file.id, "permissions"],
		enabled: isUserOwner,
		queryFn: async () => {
			const res = await apiInstance.get(
				`/api/v1/files/${file.id}/permissions`,
			);
			return res.data;
		},
	});

	const {
		isOpen: isViewerOpen,
		onOpen: onViewerOpen,
		onClose: onViewerClose,
	} = useDisclosure();

	return (
		<VStack
			bgColor="gray.700"
			borderRadius="lg"
			overflow="hidden"
			spacing={0}
			flexGrow={0.5}
			alignItems="stretch"
		>
			<HStack bgColor="cyan.700" py={3} px={6}>
				<Text fontSize="xl">File Info</Text>
			</HStack>
			<VStack p={6} alignItems="stretch">
				<FileAttributes file={file} />
				<PermissionsList
					rootFolderOwner={file.owner}
					isUserOwner={isUserOwner}
					resource={file}
					resourceType="FILE"
					permissionType={file.permissionType}
					permissions={permissions}
					isLoading={isLoading}
					isSuccess={isSuccess}
					isError={isError}
				/>
				<HStack mt={10} justifyContent="flex-end">
					<CopyLinkButton
						copyValue={`${window.location.origin}/file/${file.id}`}
					/>
					<Button colorScheme="blue" onClick={onViewerOpen}>
						View File
					</Button>
					<FileViewer
						file={file}
						isViewerOpen={isViewerOpen}
						onViewerOpen={onViewerOpen}
						onViewerClose={onViewerClose}
					/>
				</HStack>
			</VStack>
		</VStack>
	);
};

export default FileInfo;
