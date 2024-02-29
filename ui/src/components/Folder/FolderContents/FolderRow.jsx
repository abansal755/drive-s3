import FolderIcon from "../../../assets/icons/FolderIcon.jsx";
import RenameFolderButton from "./FolderRow/RenameFolderButton.jsx";
import DeleteFolderButton from "./FolderRow/DeleteFolderButton.jsx";
import { Tr, Td, Link as ChakraLink, Text } from "@chakra-ui/react";
import { Link as ReactRouterLink } from "react-router-dom";
import epochToDateString from "../../../utils/epochToDateString.js";

const FolderRow = ({ folder, parentFolderId }) => {
	return (
		<Tr>
			<Td>
				<ChakraLink
					as={ReactRouterLink}
					to={`../folder/${folder.id}`}
					display="flex"
				>
					<FolderIcon boxSize={5} mr={1} />
					<Text>{folder.folderName}</Text>
				</ChakraLink>
			</Td>
			<Td>Folder</Td>
			<Td>{epochToDateString(folder.createdAt)}</Td>
			<Td></Td>
			<Td>
				<RenameFolderButton
					folder={folder}
					parentFolderId={parentFolderId}
				/>
				<DeleteFolderButton
					folder={folder}
					parentFolderId={parentFolderId}
				/>
			</Td>
		</Tr>
	);
};

export default FolderRow;
