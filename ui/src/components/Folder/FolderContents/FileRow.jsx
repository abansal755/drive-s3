import FileIcon from "../../../assets/icons/FileIcon.jsx";
import {
	Tr,
	Td,
	Link as ChakraLink,
	Text,
	HStack,
	useDisclosure,
	Button,
} from "@chakra-ui/react";
import { Link as ReactRouterLink } from "react-router-dom";
import prettyBytes from "pretty-bytes";
import epochToDateString from "../../../utils/epochToDateString.js";
import RenameFileButton from "./FileRow/RenameFileButton.jsx";
import DeleteFileButton from "./FileRow/DeleteFileButton.jsx";
import DownloadFileButton from "./FileRow/DownloadFileButton.jsx";
import { useTheme } from "@emotion/react";
import mime from "mime-types";
import { useMemo } from "react";
import FileViewer from "./FileRow/FileViewer.jsx";
import FileInfoButton from "./FileRow/FileInfoButton.jsx";

const FileRow = ({ file, parentFolderId, rootFolderOwner }) => {
	const theme = useTheme();
	const {
		isOpen: isViewerOpen,
		onOpen: onViewerOpen,
		onClose: onViewerClose,
	} = useDisclosure();

	return (
		<Tr
			transition="200ms"
			sx={{
				":hover": {
					bgColor: theme.colors.blue[800],
				},
			}}
		>
			<Td>
				<Button
					variant="link"
					leftIcon={<FileIcon boxSize={5} mr={-1} />}
					fontWeight="normal"
					onClick={onViewerOpen}
				>
					{file.name}
					{file.extension && `.${file.extension}`}
				</Button>
				<FileViewer
					file={file}
					isViewerOpen={isViewerOpen}
					onViewerOpen={onViewerOpen}
					onViewerClose={onViewerClose}
				/>
			</Td>
			<Td>File</Td>
			<Td>{epochToDateString(file.createdAt)}</Td>
			<Td>{file.sizeInBytes && prettyBytes(file.sizeInBytes)}</Td>
			<Td>
				<HStack spacing={3}>
					<FileInfoButton
						file={file}
						rootFolderOwner={rootFolderOwner}
					/>
					{file.permissionType === "WRITE" && (
						<RenameFileButton
							file={file}
							parentFolderId={parentFolderId}
						/>
					)}
					{file.permissionType === "WRITE" && (
						<DeleteFileButton
							file={file}
							parentFolderId={parentFolderId}
						/>
					)}
					<DownloadFileButton file={file} />
				</HStack>
			</Td>
		</Tr>
	);
};

export default FileRow;
