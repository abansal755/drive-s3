import { DownloadIcon } from "@chakra-ui/icons";
import {
	Button,
	IconButton,
	Modal,
	ModalBody,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
	Progress,
	Text,
	Tooltip,
	useDisclosure,
} from "@chakra-ui/react";
import prettyBytes from "pretty-bytes";
import { Fragment } from "react";
import useDownloadFile from "../../../../hooks/useDownloadFile";

const getSuggestedNameFromFile = (file) => {
	const { name, extension } = file;
	if (!extension) return name;
	return `${name}.${extension}`;
};

const DownloadFileButton = ({ file }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();

	const { initiateDownloadMutation, abortMutation, bytesDownloaded, status } =
		useDownloadFile(file, onClose);

	const downloadBtnClickHandler = async () => {
		try {
			const fileHandle = await window.showSaveFilePicker({
				suggestedName: getSuggestedNameFromFile(file),
			});
			const writableStream = await fileHandle.createWritable();

			onOpen();
			initiateDownloadMutation.mutate(writableStream);
		} catch (err) {
			console.error(err);
		}
	};

	return (
		<Fragment>
			<Tooltip label="Download File" hasArrow>
				<IconButton
					icon={<DownloadIcon boxSize={5} />}
					colorScheme="blue"
					size="sm"
					onClick={downloadBtnClickHandler}
				/>
			</Tooltip>
			<Modal
				isOpen={isOpen}
				onClose={onClose}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Downloading File</ModalHeader>
					<ModalBody>
						<Progress
							value={(bytesDownloaded / file.sizeInBytes) * 100}
						/>
						<Text mt={2}>
							Downloaded {prettyBytes(bytesDownloaded)} out of{" "}
							{file.sizeInBytes && prettyBytes(file.sizeInBytes)}
						</Text>
					</ModalBody>
					<ModalFooter>
						<Button
							colorScheme="red"
							onClick={abortMutation.mutate}
							isLoading={status === "ABORTING"}
							loadingText="Aborting"
							isDisabled={status !== "DOWNLOADING"}
						>
							Abort
						</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default DownloadFileButton;
