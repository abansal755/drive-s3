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
		onSuccess: () =>
			queryClient.invalidateQueries({
				queryKey: ["folder", folderId, "contents"],
			}),
	});

	const formSubmitHandler = (e) => {
		e.preventDefault();
		mutation.mutate(folderName);
		modalCloseHandler();
	};

	const modalCloseHandler = () => {
		setFolderName("");
		onClose();
	};

	return (
		<Fragment>
			<MenuItem onClick={onOpen}>Add a new folder</MenuItem>
			<Modal
				isOpen={isOpen}
				onClose={modalCloseHandler}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent as="form" onSubmit={formSubmitHandler}>
					<ModalHeader>New Folder</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						<Input
							placeholder="Folder Name"
							variant="filled"
							value={folderName}
							onChange={(e) => setFolderName(e.target.value)}
						/>
					</ModalBody>
					<ModalFooter>
						<Button mr={3} colorScheme="teal" type="submit">
							Create
						</Button>
						<Button onClick={modalCloseHandler}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default NewFolderButton;
