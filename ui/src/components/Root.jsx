import { ChevronDownIcon } from "@chakra-ui/icons";
import {
	Flex,
	Box,
	Container,
	Menu,
	MenuButton,
	Button,
	MenuList,
	MenuItem,
	Avatar,
	Link as ChakraLink,
	Text,
} from "@chakra-ui/react";
import { Outlet } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import { Link as ReactRouterLink } from "react-router-dom";
import HDDIcon from "../assets/icons/HDDIcon";
import NavButton from "./Root/NavButton";

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
				>
					<ChakraLink
						as={ReactRouterLink}
						to="/"
						fontWeight="bold"
						fontSize="xl"
						display="flex"
						alignItems="center"
					>
						<HDDIcon boxSize={6} />
						<Text>Drive</Text>
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
			<NavButton />
		</Flex>
	);
};

export default Root;
