import { Fragment, useState, useEffect } from "react";
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
	Stack,
	Box,
} from "@chakra-ui/react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiInstance } from "../../../lib/axios.js";

const getNameAndExtensionFromFullName = (fileName) => {
	let name = "";
	let extension = "";
	const splits = fileName.split(".");
	if (splits.length === 1) {
		name = fileName;
	} else {
		const length = splits.length;
		name = splits.slice(0, length - 1).join("");
		extension = splits[length - 1];
	}
	return [name, extension];
};

const NewFileButton = ({ folderId }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [name, setName] = useState("");
	const [extension, setExtension] = useState("");
	const [file, setFile] = useState(null);
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: async ({ name, extension }) => {
			const {
				data: { uploadId },
			} = await apiInstance.post(`/api/v1/files`, {
				name,
				extension,
				parentFolderId: folderId,
			});
			await fetch(
				`${import.meta.env.VITE_API_SERVICE_URI}/api/v1/uploads/${uploadId}`,
				{
					method: "PUT",
					body: file.stream(),
					duplex: "half",
					credentials: "include",
				},
			);
		},
		onSuccess: () => {
			queryClient.invalidateQueries("folder", folderId, "contents");
			modalCloseHandler();
		},
	});

	useEffect(() => {
		if (!file) {
			setName("");
			setExtension("");
			return;
		}
		const [name, extension] = getNameAndExtensionFromFullName(file.name);
		setName(name);
		setExtension(extension);
	}, [file]);

	const fileChangeHandler = (e) => {
		const { files } = e.target;
		if (files.length === 0) setFile(null);
		else setFile(files[0]);
	};

	const modalCloseHandler = () => {
		onClose();
		setFile(null);
	};

	const disabled = !file || mutation.isPending;

	return (
		<Fragment>
			<MenuItem onClick={onOpen}>Upload a new file</MenuItem>
			<Modal
				isOpen={isOpen}
				onClose={modalCloseHandler}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>New File</ModalHeader>
					<ModalCloseButton isDisabled={mutation.isPending} />
					<ModalBody>
						<Stack>
							<input
								type="file"
								onChange={fileChangeHandler}
								disabled={mutation.isPending}
							/>
							<Box display="flex" alignItems="flex-end">
								<Input
									placeholder="Name"
									variant="filled"
									value={name}
									onChange={(e) => setName(e.target.value)}
									mr={2}
									isDisabled={disabled}
								/>
								.
								<Input
									placeholder="Extension"
									variant="filled"
									ml={2}
									value={extension}
									onChange={(e) =>
										setExtension(e.target.value)
									}
									isDisabled={disabled}
								/>
							</Box>
						</Stack>
					</ModalBody>
					<ModalFooter>
						<Button
							mr={3}
							colorScheme="teal"
							type="submit"
							isDisabled={disabled}
							onClick={() => mutation.mutate({ name, extension })}
							isLoading={mutation.isPending}
							loadingText="Uploading"
						>
							Upload
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

export default NewFileButton;
