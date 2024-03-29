import {
	Box,
	Button,
	Modal,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import useDownloadFile from "../../../../../hooks/useDownloadFile";
import ReactPanZoom from "react-image-pan-zoom-rotate";
import { useTheme } from "@emotion/react";

const ImageViewer = ({ file, isViewerOpen, onViewerOpen, onViewerClose }) => {
	const { initiateDownloadMutation, status, abortMutation } =
		useDownloadFile(file);
	const [contents, setContents] = useState([]);

	useEffect(() => {
		if (!isViewerOpen) return;
		(async () => {
			try {
				const { readable, writable } = new TransformStream();
				const reader = readable.getReader();
				initiateDownloadMutation.mutate(writable);
				while (true) {
					const { value, done } = await reader.read();
					if (done) break;
					setContents((prev) => [...prev, value]);
				}
			} catch (err) {
				console.error(err);
			}
		})();
	}, [isViewerOpen]);

	const closeBtnClickHandler = () => {
		if (status === "DOWNLOADING") abortMutation.mutate();
		else if (status === "DOWNLOADED") {
			onViewerClose();
			setContents([]);
		}
	};

	useEffect(() => {
		if (status === "ABORTED") {
			onViewerClose();
			setContents([]);
		}
	}, [status]);

	const theme = useTheme();

	return (
		<Modal
			isOpen={isViewerOpen}
			onClose={onViewerClose}
			size="xl"
			closeOnOverlayClick={false}
			isCentered
		>
			<ModalOverlay />
			<ModalContent>
				<ModalHeader>
					{file.name}
					{file.extension && `.${file.extension}`}
				</ModalHeader>
				<ModalBody>
					<Box
						overflow="hidden"
						bgColor={theme.colors.gray[800]}
						position="relative"
					>
						{contents && contents.length > 0 && (
							<ReactPanZoom
								image={URL.createObjectURL(new Blob(contents))}
							/>
						)}
					</Box>
				</ModalBody>
				<ModalFooter>
					<Button
						colorScheme="blue"
						onClick={closeBtnClickHandler}
						isDisabled={status === "STARTING"}
						loadingText="Closing"
						isLoading={status === "ABORTING"}
					>
						Close
					</Button>
				</ModalFooter>
				<ModalCloseButton
					isDisabled={status === "STARTING" || status === "ABORTING"}
				/>
			</ModalContent>
		</Modal>
	);
};

export default ImageViewer;
