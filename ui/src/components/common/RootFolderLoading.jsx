import { Center, Spinner } from "@chakra-ui/react";

const RootFolderLoading = () => {
    return (
        <Center w='100%' h='100%'>
			<Spinner color="teal" size='xl' speed='0.8s' thickness={4}/>
		</Center>
    )
}

export default RootFolderLoading;