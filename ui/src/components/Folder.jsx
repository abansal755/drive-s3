import { Stack, Container } from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import { apiInstance } from "../lib/axios";
import Header from "./Folder/Header";

const Folder = () => {
	const { folderId } = useParams();
	const {
		data: folderContents,
		isLoading: folderContentsLoading,
		isSuccess: folderContentsSuccess,
	} = useQuery({
		queryKey: ["folder", folderId, "contents"],
		queryFn: async () => {
			const { data: folderContents } = await apiInstance.get(
				`/api/v1/folders/${folderId}/contents`
			);
			return folderContents;
		},
	});
	const {
		data: folderAncestors,
		isLoading: folderAncestorsLoading,
		isSuccess: folderAncestorsSuccess,
	} = useQuery({
		queryKey: ["folder", folderId, "ancestors"],
		queryFn: async () => {
			const { data: folderAncestors } = await apiInstance.get(
				`/api/v1/folders/${folderId}/ancestors`
			);
			return folderAncestors;
		},
	});

	return (
		<Container maxW="container.xl" h="100%" px={0} py={10}>
			<Stack h="100%" bgColor="gray.700" borderRadius='lg' overflow='hidden'>
				{folderAncestorsSuccess && (
					<Header
						ancestors={folderAncestors.ancestors}
						rootFolderOwner={folderAncestors.rootFolderOwner}
					/>
				)}
			</Stack>
		</Container>
	);
};

export default Folder;
