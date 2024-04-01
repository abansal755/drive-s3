import FolderIcon from "../../../assets/icons/FolderIcon.jsx";
import RenameFolderButton from "../RenameFolderButton";
import DeleteFolderButton from "./FolderRow/DeleteFolderButton.jsx";
import {
	Tr,
	Td,
	Link as ChakraLink,
	Text,
	HStack,
	ButtonGroup,
} from "@chakra-ui/react";
import { Link as ReactRouterLink } from "react-router-dom";
import epochToDateString from "../../../utils/epochToDateString.js";
import { useQuery } from "@tanstack/react-query";
import { apiInstance } from "../../../lib/axios.js";
import prettyBytes from "pretty-bytes";
import { useTheme } from "@emotion/react";
import FolderInfoButton from "../FolderInfoButton";

const FolderRow = ({ folder, parentFolderId, rootFolderOwner }) => {
	const { data: sizeInBytes, isSuccess } = useQuery({
		queryKey: ["folder", folder.id, "size"],
		queryFn: async () => {
			const {
				data: { sizeInBytes },
			} = await apiInstance.get(`/api/v1/folders/${folder.id}/size`);
			return sizeInBytes;
		},
	});

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
					to={`../folder/${folder.id}`}
					display="flex"
				>
					<FolderIcon boxSize={5} mr={1} />
					<Text>{folder.folderName}</Text>
				</ChakraLink>
			</Td>
			<Td>Folder</Td>
			<Td>{epochToDateString(folder.createdAt)}</Td>
			<Td>{isSuccess && prettyBytes(sizeInBytes)}</Td>
			<Td>
				<ButtonGroup isAttached>
					<FolderInfoButton
						folder={folder}
						rootFolderOwner={rootFolderOwner}
						permissionType={folder.permissionType}
						iconBtnProps={{
							size: "sm",
							colorScheme: "blue",
						}}
					/>
					{folder.permissionType === "WRITE" && (
						<RenameFolderButton
							folder={folder}
							parentFolderId={parentFolderId}
							queriesToInvalidate={[
								["folder", parentFolderId, "contents"],
							]}
							colorScheme="blue"
						/>
					)}
					{folder.permissionType === "WRITE" && (
						<DeleteFolderButton
							folder={folder}
							parentFolderId={parentFolderId}
						/>
					)}
				</ButtonGroup>
			</Td>
		</Tr>
	);
};

export default FolderRow;
