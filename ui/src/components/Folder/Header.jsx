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

const Header = ({ ancestors, rootFolderOwner }) => {
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
				{ancestors.map((ancestor, idx) => (
					<BreadcrumbItem key={ancestor.id}>
						{!ancestor.folderName && (
							<Avatar
								name={`${rootFolderOwner.firstName} ${rootFolderOwner.lastName}`}
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
									idx === ancestors.length - 1
										? "medium"
										: "normal"
								}
							>
								{!!ancestor.folderName
									? ancestor.folderName
									: rootFolderOwner.email}
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
