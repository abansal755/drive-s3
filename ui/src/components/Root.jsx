import { ChevronDownIcon } from "@chakra-ui/icons";
import {
	Flex,
	Box,
	Container,
	Heading,
	Menu,
	MenuButton,
	Button,
	MenuList,
	MenuItem,
	Avatar,
	Link as ChakraLink,
	HStack,
} from "@chakra-ui/react";
import { Outlet } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import { Link as ReactRouterLink } from "react-router-dom";

const Root = () => {
	const { user, logout } = useAuthContext();
	const userFullName = `${user.firstName} ${user.lastName}`;

	return (
		<Flex h="100vh" w="100vw" flexDirection="column">
			<Box bgColor="teal" py={3}>
				<Container
					maxW="container.xl"
					display="flex"
					flexDirection="row"
					justifyContent="space-between"
					alignItems="center"
					px={0}
				>
					<ChakraLink
						as={ReactRouterLink}
						to="/"
						fontWeight="bold"
						fontSize="xl"
					>
						Drive
					</ChakraLink>
					<Menu>
						<MenuButton
							as={Button}
							rightIcon={<ChevronDownIcon />}
							leftIcon={
								<Avatar name={userFullName} size="xs" mr={2} />
							}
						>
							{userFullName}
						</MenuButton>
						<MenuList>
							<MenuItem>Profile</MenuItem>
							<MenuItem>Settings</MenuItem>
							<MenuItem onClick={logout}>Logout</MenuItem>
						</MenuList>
					</Menu>
				</Container>
			</Box>
			<Box flexGrow={1}>
				<Outlet />
			</Box>
		</Flex>
	);
};

export default Root;
