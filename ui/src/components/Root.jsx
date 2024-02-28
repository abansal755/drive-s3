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
	Text,
} from "@chakra-ui/react";
import { useAuthContext } from "../context/AuthContext";

const Root = () => {
	const { user, logout } = useAuthContext();
	const userFullName = `${user.firstName} ${user.lastName}`;

	return (
		<Flex h="100vh" w="100vw" flexDirection="column">
			<Box bgColor="teal" py={3}>
				<Container
					maxW="container.lg"
					display="flex"
					flexDirection="row"
					justifyContent="space-between"
					alignItems="center"
				>
					<Heading size="md">Drive</Heading>
					<Menu>
						<MenuButton as={Button} rightIcon={<ChevronDownIcon />}>
							<Box display="flex" alignItems="center">
								<Avatar name={userFullName} size="xs" mr={2} />
								<Text>{userFullName}</Text>
							</Box>
						</MenuButton>
						<MenuList>
							<MenuItem>Profile</MenuItem>
							<MenuItem>Settings</MenuItem>
							<MenuItem onClick={logout}>Logout</MenuItem>
						</MenuList>
					</Menu>
				</Container>
			</Box>
		</Flex>
	);
};

export default Root;
