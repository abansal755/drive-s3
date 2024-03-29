import { InfoIcon } from "@chakra-ui/icons";
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
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { Fragment } from "react";
import { useAuthContext } from "../../../../context/AuthContext";
import { apiInstance } from "../../../../lib/axios";
import PermissionsList from "../../FolderInfoButton/PermissionsList";
import FileAttributes from "./FileInfoButton/FileAttributes";

const FileInfoButton = ({ file, rootFolderOwner }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const { user } = useAuthContext();
	const isUserOwner = user.id === rootFolderOwner.id;

	const {
		data: permissions,
		isSuccess,
		isLoading,
		isError,
	} = useQuery({
		queryKey: ["file", file.id, "permissions"],
		enabled: isUserOwner && isOpen,
		queryFn: async () => {
			const res = await apiInstance.get(
				`/api/v1/files/${file.id}/permissions`,
			);
			return res.data;
		},
	});

	return (
		<Fragment>
			<Tooltip label="File info">
				<IconButton
					icon={<InfoIcon boxSize={5} />}
					size="sm"
					colorScheme="blue"
					onClick={onOpen}
				/>
			</Tooltip>
			<Modal
				isOpen={isOpen}
				onClose={onClose}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>File Info</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						<FileAttributes file={file} />
						<PermissionsList
							rootFolderOwner={rootFolderOwner}
							isUserOwner={isUserOwner}
							resource={file}
							resourceType="FILE"
							permissionType={file.permissionType}
							permissions={permissions}
							isLoading={isLoading}
							isSuccess={isSuccess}
							isError={isError}
						/>
					</ModalBody>
					<ModalFooter>
						<Button onClick={onClose}>Close</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default FileInfoButton;
