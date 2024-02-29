import { Fragment, useState } from "react";
import PencilSquareIcon from "../../../assets/icons/PencilSquareIcon.jsx";
import {
	Button,
	IconButton,
	Input,
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
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiInstance } from "../../../lib/axios.js";

const RenameFolderButton = ({ folder, parentFolderId }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [folderName, setFolderName] = useState("");
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: async (folderName) => {
			await apiInstance.patch(`/api/v1/folders/${folder.id}`, {
				folderName,
			});
		},
		onSuccess: () =>
			queryClient.invalidateQueries({
				queryKey: ["folder", parentFolderId, "contents"],
			}),
	});

	const formSubmitHandler = (e) => {
		e.preventDefault();
		mutation.mutate(folderName);
		modalCloseHandler();
	};

	const modalOpenHandler = () => {
		onOpen();
		setFolderName(folder.folderName);
	};

	const modalCloseHandler = () => {
		setFolderName("");
		onClose();
	};

	return (
		<Fragment>
			<Tooltip label="Rename Folder" hasArrow>
				<IconButton
					icon={<PencilSquareIcon boxSize={5} />}
					mr={3}
					colorScheme="blue"
					onClick={modalOpenHandler}
				/>
			</Tooltip>
			<Modal isOpen={isOpen} onClose={modalCloseHandler}>
				<ModalOverlay />
				<ModalContent as="form" onSubmit={formSubmitHandler}>
					<ModalHeader>Rename Folder</ModalHeader>
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
							Rename
						</Button>
						<Button onClick={modalCloseHandler}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default RenameFolderButton;
