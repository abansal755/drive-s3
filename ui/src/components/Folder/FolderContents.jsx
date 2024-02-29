import { Stack, Table, Thead, Tr, Th, Tbody, Progress } from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { Fragment } from "react";
import { apiInstance } from "../../lib/axios";
import FolderRow from "./FolderContents/FolderRow.jsx";
import FileRow from "./FolderContents/FileRow.jsx";

const FolderContents = ({ folderId }) => {
	const { data, isLoading, isSuccess } = useQuery({
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
						<Th>Actions</Th>
					</Tr>
				</Thead>
				<Tbody>
					{isSuccess && (
						<Fragment>
							{data.folders.map((folder) => (
								<FolderRow
									folder={folder}
									parentFolderId={folderId}
									key={`folder-${folder.id}`}
								/>
							))}
							{data.files.map((file) => (
								<FileRow
									file={file}
									key={`file-${file.id}`}
									parentFolderId={folderId}
								/>
							))}
						</Fragment>
					)}
				</Tbody>
			</Table>
		</Stack>
	);
};

export default FolderContents;
