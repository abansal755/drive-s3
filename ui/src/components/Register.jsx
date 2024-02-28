import { ExternalLinkIcon } from "@chakra-ui/icons";
import {
	Button,
	Center,
	Container,
	Heading,
	Input,
	InputGroup,
	InputRightElement,
	Stack,
	Link as ChakraLink,
    Box,
    HStack,
} from "@chakra-ui/react";
import { useState } from "react";
import { Link as ReactRouterLink } from "react-router-dom";

const Register = () => {
	return (
		<Center h="100vh" w="100vw">
			<Container p={10} bgColor="gray.900" borderRadius="2xl">
				<Stack spacing={3}>
					<Heading size="lg" textAlign="center" mb={10}>
						Register
					</Heading>
                    <HStack>
                        <Input variant="filled" placeholder="First Name" size="lg" />
                        <Input variant="filled" placeholder="Last Name" size="lg" />
                    </HStack>
					<Input variant="filled" placeholder="Email" size="lg" />
					<Input variant="filled" placeholder="Password" size="lg" />
                    <Input variant="filled" placeholder="Confirm Password" size="lg" />
					<Button colorScheme="teal">Register</Button>
					<ChakraLink
						textAlign="center"
						as={ReactRouterLink}
						to="/login"
					>
						Already have an account? Click here to login
						<ExternalLinkIcon ml={1} />
					</ChakraLink>
				</Stack>
			</Container>
		</Center>
	);
};

export default Register;
