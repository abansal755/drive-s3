import FileIcon from "../../../assets/icons/FileIcon.jsx";
import { Tr, Td, Link as ChakraLink, Text } from "@chakra-ui/react";
import { Link as ReactRouterLink } from "react-router-dom";
import prettyBytes from "pretty-bytes";
import epochToDateString from "../../../utils/epochToDateString.js";
import RenameFileButton from "./FileRow/RenameFileButton.jsx";
import DeleteFileButton from "./FileRow/DeleteFileButton.jsx";

const FileRow = ({ file, parentFolderId }) => {
	return (
		<Tr>
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
				<RenameFileButton file={file} parentFolderId={parentFolderId} />
				<DeleteFileButton file={file} parentFolderId={parentFolderId} />
			</Td>
		</Tr>
	);
};

export default FileRow;
