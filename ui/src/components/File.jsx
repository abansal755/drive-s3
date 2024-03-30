import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import { apiInstance } from "../lib/axios";
import {
	Container,
	Alert,
	AlertIcon,
	AlertTitle,
	AlertDescription,
} from "@chakra-ui/react";
import FileInfo from "./File/FileInfo";
import Loading from "./common/Loading";

const File = () => {
	const { fileId } = useParams();

	const {
		data: file,
		isSuccess,
		isLoading,
		isError,
		error,
	} = useQuery({
		queryKey: ["file", fileId],
		queryFn: async () => {
			const { data: file } = await apiInstance.get(
				`/api/v1/files/${fileId}`,
			);
			return file;
		},
	});

	return (
		<Container maxW="lg" px={0} py={10}>
			{isLoading && <Loading />}
			{isSuccess && <FileInfo file={file} />}
			{isError && (
				<Alert status="error" display="flex" flexDir="column">
					<AlertIcon boxSize={8} />
					<AlertTitle mt={4} mb={1} fontSize="lg">
						Error fetching file details
					</AlertTitle>
					<AlertDescription>
						{error.response.data.message}
					</AlertDescription>
				</Alert>
			)}
		</Container>
	);
};

export default File;
