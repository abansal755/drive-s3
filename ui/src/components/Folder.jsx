import { Stack, Container } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import FolderContents from "./Folder/FolderContents";
import Header from "./Folder/Header";

const Folder = () => {
	const { folderId } = useParams();

	return (
		<Container maxW="container.xl" h="100%" px={0} py={10}>
			<Stack
				h="100%"
				bgColor="gray.700"
				borderRadius="lg"
				overflow="hidden"
				spacing={0}
			>
				<Header folderId={folderId} />
				<FolderContents folderId={folderId} />
			</Stack>
		</Container>
	);
};

export default Folder;
