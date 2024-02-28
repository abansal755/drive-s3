import {
	Avatar,
	Box,
	Breadcrumb,
	BreadcrumbItem,
	BreadcrumbLink,
	Button,
	Menu,
	MenuButton,
	MenuItem,
	MenuList,
	Text,
} from "@chakra-ui/react";
import { ChevronRightIcon, AddIcon } from "@chakra-ui/icons";
import { Link as ReactRouterLink } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { apiInstance } from "../../lib/axios";

const Header = ({ folderId }) => {
	const { data, isSuccess } = useQuery({
		queryKey: ["folder", folderId, "ancestors"],
		queryFn: async () => {
			const { data: folderAncestors } = await apiInstance.get(
				`/api/v1/folders/${folderId}/ancestors`
			);
			return folderAncestors;
		},
	});

	return (
		<Box
			bgColor="cyan.700"
			py={3}
			px={4}
			display="flex"
			justifyContent="space-between"
			alignItems="center"
		>
			<Breadcrumb separator={<ChevronRightIcon />}>
				{isSuccess &&
					data.ancestors.map((ancestor, idx) => (
						<BreadcrumbItem key={ancestor.id}>
							{!ancestor.folderName && (
								<Avatar
									name={`${data.rootFolderOwner.firstName} ${data.rootFolderOwner.lastName}`}
									size="sm"
									mr={2}
								/>
							)}
							<BreadcrumbLink
								as={ReactRouterLink}
								to={`../folder/${ancestor.id}`}
								display="flex"
								alignItems="center"
							>
								<Text
									fontSize="xl"
									fontWeight={
										idx === data.ancestors.length - 1
											? "medium"
											: "normal"
									}
								>
									{!!ancestor.folderName
										? ancestor.folderName
										: data.rootFolderOwner.email}
								</Text>
							</BreadcrumbLink>
						</BreadcrumbItem>
					))}
			</Breadcrumb>
			<Menu>
				<MenuButton as={Button} rightIcon={<AddIcon />}>
					New
				</MenuButton>
				<MenuList>
					<MenuItem>Upload a new file</MenuItem>
					<MenuItem>Add a new folder</MenuItem>
				</MenuList>
			</Menu>
		</Box>
	);
};

export default Header;
