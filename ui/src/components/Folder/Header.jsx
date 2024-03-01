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
import NewFolderButton from "./Header/NewFolderButton.jsx";
import NewFileButton from "./Header/NewFileButton.jsx";

const Header = ({ folderId }) => {
	const { data, isSuccess } = useQuery({
		queryKey: ["folder", folderId, "ancestors"],
		queryFn: async () => {
			const { data: folderAncestors } = await apiInstance.get(
				`/api/v1/folders/${folderId}/ancestors`,
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
							{!ancestor.parentFolderId && (
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
									{ancestor.parentFolderId
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
					<NewFileButton folderId={folderId} />
					<NewFolderButton folderId={folderId} />
				</MenuList>
			</Menu>
		</Box>
	);
};

export default Header;
