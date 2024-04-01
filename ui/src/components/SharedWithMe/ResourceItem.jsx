import { VStack, Link as ChakraLink, WrapItem } from "@chakra-ui/react";
import { Fragment } from "react";
import FileIcon from "../../assets/icons/FileIcon";
import FolderIcon from "../../assets/icons/FolderIcon";
import { Link as ReactRouterLink } from "react-router-dom";

const ResourceItem = ({ permission }) => {
	return (
		<WrapItem w="160px" h="160px" key={permission.id}>
			<VStack w="100%" h="100%" overflow="hidden">
				{permission.resourceType === "FILE" && (
					<Fragment>
						<FileIcon boxSize="60px" />
						<ChakraLink
							as={ReactRouterLink}
							wordBreak="break-all"
							to={`/file/${permission.file.id}`}
						>
							{permission.file.name}
							{permission.file.extension &&
								`.${permission.file.extension}`}
						</ChakraLink>
					</Fragment>
				)}
				{permission.resourceType === "FOLDER" && (
					<Fragment>
						<FolderIcon boxSize="60px" />
						<ChakraLink
							as={ReactRouterLink}
							wordBreak="break-all"
							to={`/folder/${permission.folder.id}`}
						>
							{permission.folder.folderName}
						</ChakraLink>
					</Fragment>
				)}
			</VStack>
		</WrapItem>
	);
};

export default ResourceItem;
