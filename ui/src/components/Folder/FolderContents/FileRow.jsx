import FileIcon from "../../../assets/icons/FileIcon.jsx";
import { Td, useDisclosure, Button, ButtonGroup } from "@chakra-ui/react";
import { Tr } from "../../common/framerMotionWrappers";
import prettyBytes from "pretty-bytes";
import epochToDateString from "../../../utils/epochToDateString.js";
import RenameFileButton from "./FileRow/RenameFileButton.jsx";
import DeleteFileButton from "./FileRow/DeleteFileButton.jsx";
import DownloadFileButton from "./FileRow/DownloadFileButton.jsx";
import { useTheme } from "@emotion/react";
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
			whileHover={{ backgroundColor: theme.colors.blue[800] }}
			initial={{ opacity: 0, x: "-20%" }}
			animate={{ opacity: 1, x: 0 }}
			layout
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
				<ButtonGroup isAttached>
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
				</ButtonGroup>
			</Td>
		</Tr>
	);
};

export default FileRow;
