import { Fragment, useEffect, useState } from "react";
import {
	Button,
	Modal,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
	useDisclosure,
} from "@chakra-ui/react";
import { InfoIcon } from "@chakra-ui/icons";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuthContext } from "../../../context/AuthContext";
import { apiInstance } from "../../../lib/axios";
import CopyLinkButton from "./FolderInfoButton/CopyLinkButton";
import FolderAttributes from "./FolderInfoButton/FolderAttributes";
import PermissionsList from "./FolderInfoButton/PermissionsList";

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
		enabled: isUserOwner && isOpen,
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
						<FolderAttributes
							folder={folder}
							sizeInBytes={sizeInBytes}
						/>
						<PermissionsList
							rootFolderOwner={rootFolderOwner}
							isUserOwner={isUserOwner}
							folder={folder}
							permissionType={permissionType}
							permissions={permissions}
							isLoading={isLoading}
							isSuccess={isSuccess}
							isError={isError}
						/>
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
