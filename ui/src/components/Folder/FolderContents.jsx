import {
	Stack,
	Table,
	Thead,
	Tr,
	Th,
	Tbody,
	Progress,
	Alert,
	AlertIcon,
	AlertTitle,
	AlertDescription,
	Box,
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { apiInstance } from "../../lib/axios";
import FolderRow from "./FolderContents/FolderRow.jsx";
import FileRow from "./FolderContents/FileRow.jsx";

const FolderContents = ({ folderId, rootFolderOwner }) => {
	const { data, isLoading, isSuccess, isError, error } = useQuery({
		queryKey: ["folder", folderId, "contents"],
		queryFn: async () => {
			const { data: folderContents } = await apiInstance.get(
				`/api/v1/folders/${folderId}/contents`,
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
			overflow="auto"
			flexBasis={0}
		>
			{!isError && (
				<Progress
					isIndeterminate
					size="xs"
					visibility={isLoading ? "visible" : "hidden"}
				/>
			)}
			{isError && (
				<Alert
					status="error"
					flexDir="column"
					height="100%"
					justifyContent="center"
				>
					<AlertIcon boxSize={8} />
					<AlertTitle mt={4} mb={1} fontSize="lg">
						Error fetching contents
					</AlertTitle>
					<AlertDescription>
						{error.response.data.message}
					</AlertDescription>
				</Alert>
			)}
			{isSuccess && (
				<Table variant="simple">
					<Thead>
						<Tr>
							<Th>Name</Th>
							<Th>Type</Th>
							<Th>Created At</Th>
							<Th>Size</Th>
							<Th>Actions</Th>
						</Tr>
					</Thead>
					<Tbody>
						{data.folders.map((folder) => (
							<FolderRow
								folder={folder}
								parentFolderId={folderId}
								key={`folder-${folder.id}`}
								rootFolderOwner={rootFolderOwner}
							/>
						))}
						{data.files.map((file) => (
							<FileRow
								file={file}
								key={`file-${file.id}`}
								parentFolderId={folderId}
							/>
						))}
					</Tbody>
				</Table>
			)}
		</Stack>
	);
};

export default FolderContents;
