import { Fragment, useState } from "react";
import PencilSquareIcon from "../../../../assets/icons/PencilSquareIcon.jsx";
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
import { apiInstance } from "../../../../lib/axios.js";

const RenameFolderButton = ({ folder, queriesToInvalidate, colorScheme }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [folderName, setFolderName] = useState("");
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: async (folderName) => {
			await apiInstance.patch(`/api/v1/folders/${folder.id}`, {
				folderName,
			});
		},
		onSuccess: () => {
			if (queriesToInvalidate)
				queriesToInvalidate.forEach((queryKey) =>
					queryClient.invalidateQueries({
						queryKey,
					}),
				);
			onClose();
		},
	});

	const formSubmitHandler = (e) => {
		e.preventDefault();
		mutation.mutate(folderName);
	};

	const modalOpenHandler = () => {
		onOpen();
		setFolderName(folder.folderName);
	};

	return (
		<Fragment>
			<Tooltip label="Rename Folder" hasArrow>
				<IconButton
					icon={<PencilSquareIcon boxSize={5} />}
					mr={3}
					colorScheme={colorScheme}
					onClick={modalOpenHandler}
					size="sm"
				/>
			</Tooltip>
			<Modal
				isOpen={isOpen}
				onClose={onClose}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent as="form" onSubmit={formSubmitHandler}>
					<ModalHeader>Rename Folder</ModalHeader>
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
							colorScheme="teal"
							type="submit"
							isLoading={mutation.isPending}
							loadingText="Renaming"
						>
							Rename
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

export default RenameFolderButton;
