import { Table, Tbody, Tr, Td } from "@chakra-ui/react";
import epochToDateString from "../../../utils/epochToDateString";
import prettyBytes from "pretty-bytes";

const FolderAttributes = ({ folder, sizeInBytes }) => {
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
				{folder.folderName && (
					<Tr>
						<Td>Folder Name:</Td>
						<Td>{folder.folderName}</Td>
					</Tr>
				)}
				<Tr>
					<Td>Created At:</Td>
					<Td>{epochToDateString(folder.createdAt)}</Td>
				</Tr>
				<Tr>
					<Td>Folder Size:</Td>
					<Td>{prettyBytes(sizeInBytes)}</Td>
				</Tr>
			</Tbody>
		</Table>
	);
};

export default FolderAttributes;
