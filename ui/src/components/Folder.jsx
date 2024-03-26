import { Stack, Container } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import FolderContents from "./Folder/FolderContents";
import Header from "./Folder/Header";
import { useQuery } from "@tanstack/react-query";
import { apiInstance } from "../lib/axios";

const Folder = () => {
	const { folderId } = useParams();
	const { data, isSuccess } = useQuery({
		queryKey: ["folder", folderId, "ancestors"],
		queryFn: async () => {
			const { data: folderAncestors } = await apiInstance.get(
				`/api/v1/folders/${folderId}/ancestors`,
			);
			return folderAncestors;
		},
	});

	return (
		<Container maxW="container.xl" h="100%" px={0} py={10}>
			<Stack
				h="100%"
				bgColor="gray.700"
				borderRadius="lg"
				overflow="hidden"
				spacing={0}
			>
				{isSuccess && (
					<Header
						ancestors={data.ancestors}
						rootFolderOwner={data.rootFolderOwner}
						permissionType={data.permissionType}
					/>
				)}
				<FolderContents folderId={folderId} />
			</Stack>
		</Container>
	);
};

export default Folder;
