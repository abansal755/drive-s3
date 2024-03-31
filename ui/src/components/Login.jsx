import { ExternalLinkIcon } from "@chakra-ui/icons";
import {
	AlertDescription,
	AlertIcon,
	AlertTitle,
	Button,
	Center,
	Container,
	Input,
	InputGroup,
	InputRightElement,
	Link as ChakraLink,
	Stack,
} from "@chakra-ui/react";
import {
	Alert,
	Stack as FramerStack,
	Heading,
} from "./common/framerMotionWrappers";
import { useState } from "react";
import { Link as ReactRouterLink } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import GithubIcon from "../assets/icons/GithubIcon.jsx";
import { AnimatePresence } from "framer-motion";

const Login = () => {
	const [showPass, setShowPass] = useState(false);
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const { login } = useAuthContext();

	const toggleShowPass = () => {
		setShowPass((prev) => !prev);
	};

	const formSubmitHandler = (e) => {
		e.preventDefault();
		login.mutate({
			email,
			password,
		});
	};

	return (
		<Center h="100vh" w="100vw">
			<Container p={10} bgColor="gray.700" borderRadius="2xl">
				<Stack spacing={3} as="form" onSubmit={formSubmitHandler}>
					<Heading size="lg" textAlign="center" mb={10} layout>
						Login
					</Heading>
					<AnimatePresence>
						{login.isError && (
							<Alert
								status="error"
								initial={{ opacity: 0, scale: 0 }}
								animate={{ opacity: 1, scale: 1 }}
								exit={{ opacity: 0, scale: 0 }}
							>
								<AlertIcon />
								<AlertTitle>
									{login.error.response.data.message}
								</AlertTitle>
							</Alert>
						)}
					</AnimatePresence>
					<FramerStack layout>
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
								<Button
									size="xs"
									mr={2}
									onClick={toggleShowPass}
								>
									{showPass ? "Hide" : "Show"}
								</Button>
							</InputRightElement>
						</InputGroup>
						<Button
							colorScheme="blue"
							type="submit"
							isLoading={login.isPending}
						>
							Login
						</Button>
						<Button
							as={ReactRouterLink}
							to={`${import.meta.env.VITE_AUTH_SERVICE_URI}/login/oauth2/github`}
							leftIcon={<GithubIcon boxSize={6} />}
							isDisabled={login.isPending}
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
					</FramerStack>
				</Stack>
			</Container>
		</Center>
	);
};

export default Login;
