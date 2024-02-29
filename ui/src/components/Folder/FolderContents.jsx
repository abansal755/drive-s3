import {
	Stack,
	Table,
	Thead,
	Tr,
	Th,
	Tbody,
	Td,
	Link as ChakraLink,
	Progress,
	Text,
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { Fragment } from "react";
import { Link as ReactRouterLink } from "react-router-dom";
import FileIcon from "../../assets/icons/FileIcon";
import FolderIcon from "../../assets/icons/FolderIcon";
import { apiInstance } from "../../lib/axios";
import prettyBytes from 'pretty-bytes';

const epochToDateString = (epoch) => {
	const date = new Date(epoch);
	return `${date.toLocaleTimeString()}, ${date.toLocaleDateString()}`;
};

const FolderContents = ({ folderId }) => {
	const { data, isLoading, isSuccess } = useQuery({
		queryKey: ["folder", folderId, "contents"],
		queryFn: async () => {
			const { data: folderContents } = await apiInstance.get(
				`/api/v1/folders/${folderId}/contents`
			);
			return folderContents;
		},
	});

	return (
		<Stack
			spacing={0}
			flexGrow={1}
			flexShrink={1}
			minH={0}
			overflow="scroll"
			flexBasis={0}
		>
			<Progress
				isIndeterminate
				size="xs"
				visibility={isLoading ? "visible" : "hidden"}
			/>
			<Table variant="simple">
				<Thead>
					<Tr>
						<Th>Name</Th>
						<Th>Type</Th>
						<Th>Created At</Th>
						<Th>Size</Th>
					</Tr>
				</Thead>
				<Tbody>
					{isSuccess && (
						<Fragment>
							{data.folders.map((folder) => (
								<Tr key={`folder-${folder.id}`}>
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
									<Td>
										{epochToDateString(folder.createdAt)}
									</Td>
								</Tr>
							))}
							{data.files.map((file) => (
								<Tr key={`file-${file.id}`}>
									<Td>
										<ChakraLink
											as={ReactRouterLink}
											to={`../file/${file.id}`}
											display="flex"
										>
											<FileIcon boxSize={5} mr={1} />
											<Text>
												{file.name}.{file.extension}
											</Text>
										</ChakraLink>
									</Td>
									<Td>File</Td>
									<Td>{epochToDateString(file.createdAt)}</Td>
									<Td>{file.sizeInBytes && prettyBytes(file.sizeInBytes)}</Td>
								</Tr>
							))}
						</Fragment>
					)}
				</Tbody>
			</Table>
		</Stack>
	);
};

export default FolderContents;
