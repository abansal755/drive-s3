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
		onSuccess: () =>
			queryClient.invalidateQueries({
				queryKey: ["folder", parentFolderId, "contents"],
			}),
	});

	const formSubmitHandler = (e) => {
		e.preventDefault();
		mutation.mutate({ name, extension });
		onClose();
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
					mr={3}
					colorScheme="blue"
					onClick={modalOpenHandler}
				/>
			</Tooltip>
			<Modal isOpen={isOpen} onClose={onClose}>
				<ModalOverlay />
				<ModalContent as="form" onSubmit={formSubmitHandler}>
					<ModalHeader>Rename File</ModalHeader>
					<ModalCloseButton />
					<ModalBody display="flex" alignItems="flex-end">
						<Input
							placeholder="Name"
							variant="filled"
							value={name}
							onChange={(e) => setName(e.target.value)}
							mr={2}
						/>
						.
						<Input
							placeholder="Extension"
							variant="filled"
							ml={2}
							value={extension}
							onChange={(e) => setExtension(e.target.value)}
						/>
					</ModalBody>
					<ModalFooter>
						<Button mr={3} colorScheme="teal" type="submit">
							Rename
						</Button>
						<Button onClick={onClose}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default RenameFileButton;
