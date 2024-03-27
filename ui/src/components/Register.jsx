import { ExternalLinkIcon } from "@chakra-ui/icons";
import {
	Button,
	Center,
	Container,
	Heading,
	Input,
	Stack,
	Link as ChakraLink,
	HStack,
	Alert,
	AlertIcon,
	AlertTitle,
} from "@chakra-ui/react";
import { useState } from "react";
import { Link as ReactRouterLink } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext.jsx";
import GithubIcon from "../assets/icons/GithubIcon.jsx";

const Register = () => {
	const [firstName, setFirstName] = useState("");
	const [lastName, setLastName] = useState("");
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const [confirmPassword, setConfirmPassword] = useState("");
	const { register } = useAuthContext();

	const formSubmitHandler = (e) => {
		e.preventDefault();
		register.mutate({
			email,
			password,
			confirmPassword,
			firstName,
			lastName,
		});
	};

	return (
		<Center h="100vh" w="100vw">
			<Container p={10} bgColor="gray.700" borderRadius="2xl">
				<Stack spacing={3} as="form" onSubmit={formSubmitHandler}>
					<Heading size="lg" textAlign="center" mb={10}>
						Register
					</Heading>
					{register.isError && (
						<Alert status="error">
							<AlertIcon />
							<AlertTitle>
								{register.error.response.data.message}
							</AlertTitle>
						</Alert>
					)}
					<HStack>
						<Input
							variant="filled"
							placeholder="First Name"
							size="lg"
							value={firstName}
							onChange={(e) => setFirstName(e.target.value)}
						/>
						<Input
							variant="filled"
							placeholder="Last Name"
							size="lg"
							value={lastName}
							onChange={(e) => setLastName(e.target.value)}
						/>
					</HStack>
					<Input
						variant="filled"
						placeholder="Email"
						size="lg"
						value={email}
						onChange={(e) => setEmail(e.target.value)}
					/>
					<Input
						variant="filled"
						placeholder="Password"
						size="lg"
						type="password"
						value={password}
						onChange={(e) => setPassword(e.target.value)}
					/>
					<Input
						variant="filled"
						placeholder="Confirm Password"
						size="lg"
						type="password"
						value={confirmPassword}
						onChange={(e) => setConfirmPassword(e.target.value)}
					/>
					<Button
						colorScheme="teal"
						type="submit"
						isLoading={register.isPending}
					>
						Register
					</Button>
					<Button
						as={ReactRouterLink}
						to={`${import.meta.env.VITE_AUTH_SERVICE_URI}/login/oauth2/github`}
						leftIcon={<GithubIcon boxSize={6} />}
						isDisabled={register.isPending}
					>
						Register with GitHub
					</Button>
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
