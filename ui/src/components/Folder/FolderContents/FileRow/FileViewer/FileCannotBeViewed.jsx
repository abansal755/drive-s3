import {
	Alert,
	AlertIcon,
	AlertTitle,
	Button,
	Modal,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
} from "@chakra-ui/react";

const FileCannotBeViewed = ({
	file,
	isViewerOpen,
	onViewerOpen,
	onViewerClose,
}) => {
	return (
		<Modal
			isOpen={isViewerOpen}
			closeOnOverlayClick={false}
			onClose={onViewerClose}
		>
			<ModalOverlay />
			<ModalContent>
				<ModalHeader>
					{file.name}
					{file.extension && `.${file.extension}`}
				</ModalHeader>
				<ModalBody>
					<Alert status="warning">
						<AlertIcon />
						<AlertTitle>This file cannot be viewed</AlertTitle>
					</Alert>
				</ModalBody>
				<ModalFooter>
					<Button colorScheme="blue" onClick={onViewerClose}>
						Close
					</Button>
				</ModalFooter>
				<ModalCloseButton />
			</ModalContent>
		</Modal>
	);
};

export default FileCannotBeViewed;
