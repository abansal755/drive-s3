import { Table, Tbody, Td, Tr } from "@chakra-ui/react";
import prettyBytes from "pretty-bytes";
import epochToDateString from "../../../../../utils/epochToDateString";

const FileAttributes = ({ file }) => {
	return (
		<Table
			variant="simple"
			sx={{
				td: {
					py: 1,
					px: 0,
				},
			}}
		>
			<Tbody>
				<Tr>
					<Td>File Name:</Td>
					<Td>{file.name}</Td>
				</Tr>
				{file.extension && (
					<Tr>
						<Td>File Extension:</Td>
						<Td>{file.extension}</Td>
					</Tr>
				)}
				<Tr>
					<Td>Created At:</Td>
					<Td>{epochToDateString(file.createdAt)}</Td>
				</Tr>
				<Tr>
					<Td>File Size:</Td>
					<Td>{prettyBytes(file.sizeInBytes)}</Td>
				</Tr>
			</Tbody>
		</Table>
	);
};

export default FileAttributes;
