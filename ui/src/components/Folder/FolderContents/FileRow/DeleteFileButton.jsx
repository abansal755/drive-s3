import { Fragment } from "react";
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
import { DeleteIcon } from "@chakra-ui/icons";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiInstance } from "../../../../lib/axios.js";

const DeleteFileButton = ({ file, parentFolderId }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: async () => {
			await apiInstance.delete(`/api/v1/files/${file.id}`);
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ["folder", parentFolderId, "contents"],
			});
			onClose();
		},
	});

	return (
		<Fragment>
			<Tooltip label="Delete File" hasArrow>
				<IconButton
					icon={<DeleteIcon boxSize={5} />}
					colorScheme="blue"
					onClick={onOpen}
					size="sm"
				/>
			</Tooltip>
			<Modal
				isOpen={isOpen}
				onClose={onClose}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Delete File</ModalHeader>
					<ModalCloseButton isDisabled={mutation.isPending} />
					<ModalBody>
						Are you sure you want to delete this file?
					</ModalBody>
					<ModalFooter>
						<Button
							mr={3}
							colorScheme="red"
							type="submit"
							onClick={mutation.mutate}
							isLoading={mutation.isPending}
							loadingText="Deleting"
						>
							Delete
						</Button>
						<Button
							onClick={onClose}
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

export default DeleteFileButton;
