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

const RenameFileButton = ({ file, parentFolderId }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const queryClient = useQueryClient();
	const [name, setName] = useState("");
	const [extension, setExtension] = useState("");

	const mutation = useMutation({
		mutationFn: async ({ name, extension }) => {
			await apiInstance.patch(`/api/v1/files/${file.id}`, {
				name,
				extension,
			});
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ["folder", parentFolderId, "contents"],
			});
			onClose();
		},
	});

	const formSubmitHandler = (e) => {
		e.preventDefault();
		mutation.mutate({ name, extension });
	};

	const modalOpenHandler = () => {
		onOpen();
		setName(file.name);
		setExtension(file.extension);
	};

	return (
		<Fragment>
			<Tooltip label="Rename File" hasArrow>
				<IconButton
					icon={<PencilSquareIcon boxSize={5} />}
					colorScheme="blue"
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
					<ModalHeader>Rename File</ModalHeader>
					<ModalCloseButton isDisabled={mutation.isPending} />
					<ModalBody display="flex" alignItems="flex-end">
						<Input
							placeholder="Name"
							variant="filled"
							value={name}
							onChange={(e) => setName(e.target.value)}
							mr={2}
							isDisabled={mutation.isPending}
						/>
						.
						<Input
							placeholder="Extension"
							variant="filled"
							ml={2}
							value={extension}
							onChange={(e) => setExtension(e.target.value)}
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

export default RenameFileButton;
