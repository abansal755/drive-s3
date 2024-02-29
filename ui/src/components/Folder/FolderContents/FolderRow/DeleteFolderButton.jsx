import { Fragment } from "react";
import {
	Button,
	IconButton,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
	Tooltip,
	Modal,
	useDisclosure,
} from "@chakra-ui/react";
import { DeleteIcon } from "@chakra-ui/icons";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiInstance } from "../../../../lib/axios.js";

const DeleteFolderButton = ({ folder, parentFolderId }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: async () => {
			await apiInstance.delete(`/api/v1/folders/${folder.id}`);
		},
		onSuccess: () =>
			queryClient.invalidateQueries({
				queryKey: ["folder", parentFolderId, "contents"],
			}),
	});

	return (
		<Fragment>
			<Tooltip label="Delete Folder" hasArrow>
				<IconButton
					icon={<DeleteIcon boxSize={5} />}
					colorScheme="blue"
					onClick={onOpen}
				/>
			</Tooltip>
			<Modal isOpen={isOpen} onClose={onClose}>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Delete Folder</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						Are you sure you want to delete this folder?
					</ModalBody>
					<ModalFooter>
						<Button
							mr={3}
							colorScheme="red"
							type="submit"
							onClick={mutation.mutate}
						>
							Delete
						</Button>
						<Button onClick={onClose}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default DeleteFolderButton;
