import {
	Container,
	HStack,
	SimpleGrid,
	Text,
	VStack,
	Link as ChakraLink,
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { Fragment } from "react";
import FileIcon from "../assets/icons/FileIcon";
import FolderIcon from "../assets/icons/FolderIcon";
import { useAuthContext } from "../context/AuthContext";
import { apiInstance } from "../lib/axios";
import { Link as ReactRouterLink } from "react-router-dom";

const SharedWithMe = () => {
	const { user } = useAuthContext();
	const { data: permissions, isSuccess } = useQuery({
		queryKey: ["sharedWithMe", user.id],
		queryFn: async () => {
			const res = apiInstance.get("/api/v1/permissions");
			return (await res).data;
		},
	});

	return (
		<Container maxW="container.xl" h="100%" py={10}>
			<VStack
				h="100%"
				bgColor="gray.700"
				borderRadius="lg"
				overflow="hidden"
				spacing={0}
				alignItems="stretch"
			>
				<HStack
					bgColor="cyan.700"
					py={3}
					px={4}
					display="flex"
					justifyContent="space-between"
					alignItems="center"
				>
					<Text fontSize="xl" fontWeight="medium">
						Shared With Me
					</Text>
				</HStack>
				<SimpleGrid
					columns={[2, 4, 8]}
					spacing={2}
					p={8}
					flexGrow={1}
					flexShrink={1}
					minH={0}
					overflow="auto"
					flexBasis={0}
				>
					{isSuccess &&
						permissions.map((permission) => (
							<VStack key={permission.id}>
								{permission.resourceType === "FILE" && (
									<Fragment>
										<FileIcon boxSize="40px" />
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
										<FolderIcon boxSize="40px" />
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
						))}
				</SimpleGrid>
			</VStack>
		</Container>
	);
};

export default SharedWithMe;
