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
} from "@chakra-ui/react";
import { Outlet } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";

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
					<Heading size="md">Drive</Heading>
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
