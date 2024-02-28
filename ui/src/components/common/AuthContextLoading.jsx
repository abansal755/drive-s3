import { Center, Spinner } from "@chakra-ui/react";

const AuthContextLoading = () => {
    return (
        <Center w='100vw' h='100vh'>
			<Spinner color="teal" size='xl' speed='0.8s' thickness={4}/>
		</Center>
    )
}

export default AuthContextLoading;