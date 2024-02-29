import { ExternalLinkIcon } from "@chakra-ui/icons";
import {
	Button,
	Center,
	Container,
	Heading,
	Input,
	InputGroup,
	InputRightElement,
	Link as ChakraLink,
	Stack,
} from "@chakra-ui/react";
import { useState } from "react";
import { Link as ReactRouterLink } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import GithubIcon from "../assets/icons/GithubIcon.jsx";

const Login = () => {
	const [showPass, setShowPass] = useState(false);
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const authContext = useAuthContext();

	const toggleShowPass = () => {
		setShowPass((prev) => !prev);
	};

	const formSubmitHandler = (e) => {
		e.preventDefault();
		authContext.login({
			email,
			password,
		});
	};

	return (
		<Center h="100vh" w="100vw">
			<Container p={10} bgColor="gray.700" borderRadius="2xl">
				<Stack spacing={3} as="form" onSubmit={formSubmitHandler}>
					<Heading size="lg" textAlign="center" mb={10}>
						Login
					</Heading>
					<Input
						variant="filled"
						placeholder="Email"
						size="lg"
						value={email}
						onChange={(e) => setEmail(e.target.value)}
					/>
					<InputGroup size="lg">
						<Input
							variant="filled"
							placeholder="Password"
							size="lg"
							type={showPass ? "text" : "password"}
							value={password}
							onChange={(e) => setPassword(e.target.value)}
						/>
						<InputRightElement>
							<Button size="xs" mr={2} onClick={toggleShowPass}>
								{showPass ? "Hide" : "Show"}
							</Button>
						</InputRightElement>
					</InputGroup>
					<Button colorScheme="teal" type="submit">
						Login
					</Button>
					<Button
						as={ReactRouterLink}
						to={`${import.meta.env.VITE_AUTH_SERVICE_URI}/login/oauth2/github`}
						leftIcon={<GithubIcon boxSize={6} />}
					>
						Login with GitHub
					</Button>
					<ChakraLink
						textAlign="center"
						as={ReactRouterLink}
						to="/register"
					>
						Do not have an account? Click here to register
						<ExternalLinkIcon ml={1} />
					</ChakraLink>
				</Stack>
			</Container>
		</Center>
	);
};

export default Login;
