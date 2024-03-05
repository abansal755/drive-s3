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
import { Fragment, useState } from "react";

const getSuggestedNameFromFile = (file) => {
	const { name, extension } = file;
	if (!extension) return name;
	return `${name}.${extension}`;
};

const DownloadFileButton = ({ file }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [bytesDownloaded, setBytesDownloaded] = useState(0);

	const modalCloseHandler = () => {
		onClose();
		setBytesDownloaded(0);
	};

	const downloadBtnClickHandler = async () => {
		try {
			const fileHandle = await window.showSaveFilePicker({
				suggestedName: getSuggestedNameFromFile(file),
			});
			const writableStream = await fileHandle.createWritable();
			const writer = writableStream.getWriter();
			onOpen();

			const res = await fetch(
				`${import.meta.env.VITE_API_SERVICE_URI}/api/v1/files/${file.id}/download`,
				{ credentials: "include" },
			);
			const reader = res.body.getReader();

			while (true) {
				const { value, done } = await reader.read();
				if (done) break;
				setBytesDownloaded((prev) => prev + value.length);
				await writer.ready;
				await writer.write(value);
			}
			writer.close();
			modalCloseHandler();
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
				onClose={modalCloseHandler}
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
							{prettyBytes(file.sizeInBytes)}
						</Text>
					</ModalBody>
					<ModalFooter>
						<Button colorScheme="red">Abort</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default DownloadFileButton;
