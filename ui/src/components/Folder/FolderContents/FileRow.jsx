import FileIcon from "../../../assets/icons/FileIcon.jsx";
import { Tr, Td, Link as ChakraLink, Text, HStack } from "@chakra-ui/react";
import { Link as ReactRouterLink } from "react-router-dom";
import prettyBytes from "pretty-bytes";
import epochToDateString from "../../../utils/epochToDateString.js";
import RenameFileButton from "./FileRow/RenameFileButton.jsx";
import DeleteFileButton from "./FileRow/DeleteFileButton.jsx";
import DownloadFileButton from "./FileRow/DownloadFileButton.jsx";
import { useTheme } from "@emotion/react";

const FileRow = ({ file, parentFolderId }) => {
	const theme = useTheme();

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
				<ChakraLink
					as={ReactRouterLink}
					to={`../file/${file.id}`}
					display="flex"
				>
					<FileIcon boxSize={5} mr={1} />
					<Text>
						{file.name}
						{file.extension && `.${file.extension}`}
					</Text>
				</ChakraLink>
			</Td>
			<Td>File</Td>
			<Td>{epochToDateString(file.createdAt)}</Td>
			<Td>{file.sizeInBytes && prettyBytes(file.sizeInBytes)}</Td>
			<Td>
				<HStack spacing={3}>
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
