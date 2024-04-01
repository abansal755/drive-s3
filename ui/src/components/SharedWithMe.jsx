import {
	Container,
	HStack,
	SimpleGrid,
	Text,
	VStack,
	Link as ChakraLink,
	Wrap,
	Progress,
	Alert,
	AlertIcon,
	AlertTitle,
	AlertDescription,
} from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import { Fragment } from "react";
import FileIcon from "../assets/icons/FileIcon";
import FolderIcon from "../assets/icons/FolderIcon";
import { useAuthContext } from "../context/AuthContext";
import { apiInstance } from "../lib/axios";
import { Link as ReactRouterLink } from "react-router-dom";
import ResourceItem from "./SharedWithMe/ResourceItem";

const SharedWithMe = () => {
	const { user } = useAuthContext();
	const {
		data: permissions,
		isSuccess,
		isLoading,
		isError,
		error,
	} = useQuery({
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
					bgColor="blue.700"
					p={4}
					display="flex"
					justifyContent="space-between"
					alignItems="center"
				>
					<Text fontSize="xl" fontWeight="medium">
						Shared With Me
					</Text>
				</HStack>
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
							{error && error.response && error.response.data
								? error.response.data.message
								: "Something went wrong"}
						</AlertDescription>
					</Alert>
				)}
				<Wrap
					p={8}
					flexGrow={1}
					flexShrink={1}
					minH={0}
					overflow="auto"
					flexBasis={0}
				>
					{isSuccess &&
						permissions.map((permission) => (
							<ResourceItem permission={permission} />
						))}
				</Wrap>
			</VStack>
		</Container>
	);
};

export default SharedWithMe;
