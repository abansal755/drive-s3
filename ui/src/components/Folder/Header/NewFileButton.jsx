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
	Progress,
	Text,
} from "@chakra-ui/react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiInstance } from "../../../lib/axios.js";
import prettyBytes from "pretty-bytes";
import FileIcon from "../../../assets/icons/FileIcon";

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
	const [uploadId, setUploadId] = useState(null);
	const [bytesUploaded, setBytesUploaded] = useState(0);
	const queryClient = useQueryClient();

	const uploadMutation = useMutation({
		mutationFn: async ({ name, extension }) => {
			const {
				data: { uploadId },
			} = await apiInstance.post(`/api/v1/files`, {
				name,
				extension,
				parentFolderId: folderId,
			});
			setUploadId(uploadId);
			const { readable, writable } = new TransformStream();
			const promise = fetch(
				`${import.meta.env.VITE_API_SERVICE_URI}/api/v1/uploads/stream/${uploadId}`,
				{
					method: "PUT",
					body: readable,
					duplex: "half",
					credentials: "include",
					headers: {
						"x-content-length": file.size,
					},
				},
			);

			const writer = writable.getWriter();
			const reader = file.stream().getReader();
			while (true) {
				const { value, done } = await reader.read();
				if (done) break;
				setBytesUploaded((prev) => prev + value.length);
				await writer.ready;
				await writer.write(value);
			}
			writer.close();
			await promise;
		},
		onSuccess: () => {
			queryClient.invalidateQueries("folder", folderId, "contents");
			modalCloseHandler();
		},
	});

	const abortMutation = useMutation({
		mutationFn: async () => {
			await apiInstance.patch(`/api/v1/uploads/abort/${uploadId}`);
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
		setUploadId(null);
		setBytesUploaded(0);
		abortMutation.reset();
		uploadMutation.reset();
	};

	const disabled = !file || uploadMutation.isPending;

	return (
		<Fragment>
			<MenuItem onClick={onOpen} icon={<FileIcon boxSize={4} />}>
				Upload a new file
			</MenuItem>
			<Modal
				isOpen={isOpen}
				onClose={modalCloseHandler}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>New File</ModalHeader>
					<ModalCloseButton isDisabled={uploadMutation.isPending} />
					<ModalBody>
						<Stack>
							<input
								type="file"
								onChange={fileChangeHandler}
								disabled={uploadMutation.isPending}
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
							{uploadMutation.isPending && (
								<Fragment>
									<Progress
										value={
											(bytesUploaded / file.size) * 100
										}
									/>
									<Text>
										Uploaded {prettyBytes(bytesUploaded)}{" "}
										out of {prettyBytes(file.size)}
									</Text>
								</Fragment>
							)}
						</Stack>
					</ModalBody>
					<ModalFooter>
						<Button
							mr={3}
							colorScheme="blue"
							type="submit"
							isDisabled={disabled}
							onClick={() =>
								uploadMutation.mutate({ name, extension })
							}
							isLoading={uploadMutation.isPending}
							loadingText="Uploading"
						>
							Upload
						</Button>
						<Button
							onClick={abortMutation.mutate}
							isLoading={
								abortMutation.isPending ||
								abortMutation.isSuccess
							}
							isDisabled={!uploadId}
							colorScheme="red"
							loadingText="Aborting"
							type="button"
						>
							Abort
						</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default NewFileButton;
