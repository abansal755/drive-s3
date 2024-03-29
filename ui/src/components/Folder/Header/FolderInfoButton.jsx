import { Fragment, useEffect, useState } from "react";
import {
	Button,
	IconButton,
	Modal,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
	Tooltip,
	useDisclosure,
	Table,
	Tbody,
	Tr,
	Td,
	Heading,
	HStack,
	VStack,
	Alert,
	AlertIcon,
	AlertTitle,
	Progress,
	CircularProgress,
	Box,
} from "@chakra-ui/react";
import { InfoIcon } from "@chakra-ui/icons";
import epochToDateString from "../../../utils/epochToDateString";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import prettyBytes from "pretty-bytes";
import { useAuthContext } from "../../../context/AuthContext";
import { apiInstance } from "../../../lib/axios";
import PermissionRow from "./FolderInfoButton/PermissionRow";
import AddPermissionSelect from "./FolderInfoButton/AddPermissionSelect";
import CopyLinkButton from "./FolderInfoButton/CopyLinkButton";
import RootFolderLoading from "../../common/Loading";

const FolderInfoButton = ({ folder, rootFolderOwner, permissionType }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [sizeInBytes, setSizeInBytes] = useState(0);
	const queryClient = useQueryClient();
	const { user } = useAuthContext();
	const isUserOwner = user.id === rootFolderOwner.id;

	const {
		data: permissions,
		isSuccess,
		isLoading,
		isError,
	} = useQuery({
		queryKey: ["folder", folder.id, "permissions"],
		enabled: isUserOwner,
		queryFn: async () => {
			const res = await apiInstance.get(
				`/api/v1/folders/${folder.id}/permissions`,
			);
			return res.data;
		},
	});

	useEffect(() => {
		if (!isOpen) return;
		const contents = queryClient.getQueryData([
			"folder",
			folder.id.toString(),
			"contents",
		]);
		if (!contents) return;
		let size = 0;
		contents.files.forEach((file) => (size += file.sizeInBytes));
		contents.folders.forEach((folder) => {
			size += queryClient.getQueryData(["folder", folder.id, "size"]);
		});
		setSizeInBytes(size);
	}, [isOpen]);

	return (
		<Fragment>
			<Button rightIcon={<InfoIcon />} onClick={onOpen}>
				Info
			</Button>
			<Modal
				isOpen={isOpen}
				onClose={onClose}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Folder Info</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						<Table
							variant="simple"
							sx={{
								td: {
									py: 1,
									px: 0,
								},
							}}
						>
							<Tbody>
								{folder.folderName && (
									<Tr>
										<Td>Folder Name:</Td>
										<Td>{folder.folderName}</Td>
									</Tr>
								)}
								<Tr>
									<Td>Created At:</Td>
									<Td>
										{epochToDateString(folder.createdAt)}
									</Td>
								</Tr>
								<Tr>
									<Td>Folder Size:</Td>
									<Td>{prettyBytes(sizeInBytes)}</Td>
								</Tr>
							</Tbody>
						</Table>
						<Heading size="md" mt={4} mb={4}>
							Permissions
						</Heading>
						{isError && (
							<Alert status="error" mb={2}>
								<AlertIcon />
								<AlertTitle>
									Error fetching permissions
								</AlertTitle>
							</Alert>
						)}
						{isUserOwner && <AddPermissionSelect folder={folder} />}
						<VStack alignItems="start" spacing={0}>
							<PermissionRow
								permission={{
									user: rootFolderOwner,
									permissionType: "OWNER",
								}}
								isUserOwner={isUserOwner}
							/>
							{!isUserOwner && (
								<PermissionRow
									permission={{
										user,
										permissionType,
									}}
									isUserOwner={isUserOwner}
								/>
							)}
							{isLoading && <RootFolderLoading />}
							{isSuccess &&
								permissions.map((permission) => (
									<PermissionRow
										key={permission.id}
										permission={permission}
										isUserOwner={isUserOwner}
										folder={folder}
									/>
								))}
						</VStack>
					</ModalBody>
					<ModalFooter>
						<CopyLinkButton />
						<Button onClick={onClose}>Close</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default FolderInfoButton;
