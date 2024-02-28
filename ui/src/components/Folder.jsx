import {
	Stack,
	Container,
	Table,
	TableContainer,
	Thead,
	Tr,
	Th,
	Tbody,
	Td,
	Link as ChakraLink,
	Box,
	Progress,
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { Fragment } from "react";
import { useParams, Link as ReactRouterLink } from "react-router-dom";
import { apiInstance } from "../lib/axios";
import Header from "./Folder/Header";

const epochToDateString = (epoch) => {
	const date = new Date(epoch);
	return `${date.toLocaleTimeString()}, ${date.toLocaleDateString()}`;
};

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
			<Stack
				h="100%"
				bgColor="gray.700"
				borderRadius="lg"
				overflow="hidden"
				spacing={0}
			>
				{folderAncestorsSuccess && (
					<Header
						ancestors={folderAncestors.ancestors}
						rootFolderOwner={folderAncestors.rootFolderOwner}
					/>
				)}
				<Stack spacing={0}>
					<Progress
						isIndeterminate
						size="xs"
						visibility={
							folderContentsLoading ? "visible" : "hidden"
						}
					/>
					<TableContainer>
						<Table variant="simple">
							<Thead>
								<Tr>
									<Th>Name</Th>
									<Th>Type</Th>
									<Th>Created At</Th>
								</Tr>
							</Thead>
							<Tbody>
								{folderContentsSuccess && (
									<Fragment>
										{folderContents.folders.map(
											(folder) => (
												<Tr key={`folder-${folder.id}`}>
													<Td>
														<ChakraLink
															as={ReactRouterLink}
															to={`../folder/${folder.id}`}
														>
															{folder.folderName}
														</ChakraLink>
													</Td>
													<Td>Folder</Td>
													<Td>
														{epochToDateString(
															folder.createdAt
														)}
													</Td>
												</Tr>
											)
										)}
										{folderContents.files.map((file) => (
											<Tr key={`file-${file.id}`}>
												<Td>
													<ChakraLink
														as={ReactRouterLink}
														to={`../file/${file.id}`}
													>
														{file.name}.
														{file.extension}
													</ChakraLink>
												</Td>
												<Td>File</Td>
												<Td>
													{epochToDateString(
														file.createdAt
													)}
												</Td>
											</Tr>
										))}
									</Fragment>
								)}
							</Tbody>
						</Table>
					</TableContainer>
				</Stack>
			</Stack>
		</Container>
	);
};

export default Folder;
