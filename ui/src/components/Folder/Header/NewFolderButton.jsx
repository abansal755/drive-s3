import {
	Button,
	Input,
	MenuItem,
	Modal,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
	useDisclosure,
} from "@chakra-ui/react";
import { Fragment, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiInstance } from "../../../lib/axios.js";
import FolderIcon from "../../../assets/icons/FolderIcon";

const NewFolderButton = ({ folderId }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [folderName, setFolderName] = useState("");
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: async (folderName) => {
			await apiInstance.post("/api/v1/folders", {
				folderName,
				parentFolderId: folderId,
			});
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ["folder", folderId.toString(), "contents"],
			});
			modalCloseHandler();
		},
	});

	const formSubmitHandler = (e) => {
		e.preventDefault();
		mutation.mutate(folderName);
	};

	const modalCloseHandler = () => {
		setFolderName("");
		onClose();
	};

	return (
		<Fragment>
			<MenuItem onClick={onOpen} icon={<FolderIcon boxSize={4} />}>
				Add a new folder
			</MenuItem>
			<Modal
				isOpen={isOpen}
				onClose={modalCloseHandler}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent as="form" onSubmit={formSubmitHandler}>
					<ModalHeader>New Folder</ModalHeader>
					<ModalCloseButton isDisabled={mutation.isPending} />
					<ModalBody>
						<Input
							placeholder="Folder Name"
							variant="filled"
							value={folderName}
							onChange={(e) => setFolderName(e.target.value)}
							isDisabled={mutation.isPending}
						/>
					</ModalBody>
					<ModalFooter>
						<Button
							mr={3}
							colorScheme="blue"
							type="submit"
							isLoading={mutation.isPending}
							loadingText="Creating"
						>
							Create
						</Button>
						<Button
							onClick={modalCloseHandler}
							isDisabled={mutation.isPending}
						>
							Cancel
						</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default NewFolderButton;
