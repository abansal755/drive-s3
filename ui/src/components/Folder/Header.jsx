import {
	Avatar,
	Box,
	Breadcrumb,
	BreadcrumbItem,
	BreadcrumbLink,
	Button,
	HStack,
	IconButton,
	Menu,
	MenuButton,
	MenuList,
	Text,
	Tooltip,
} from "@chakra-ui/react";
import { ChevronRightIcon, AddIcon, InfoIcon } from "@chakra-ui/icons";
import { Link as ReactRouterLink } from "react-router-dom";
import NewFolderButton from "./Header/NewFolderButton.jsx";
import NewFileButton from "./Header/NewFileButton.jsx";
import FolderInfoButton from "./Header/FolderInfoButton";
import RenameFolderButton from "../common/RenameFolderButton.jsx";

const Header = ({ ancestors, rootFolderOwner, permissionType }) => {
	const folder = ancestors.at(-1);
	const folderId = folder.id;

	return (
		<Box
			bgColor="cyan.700"
			py={3}
			px={4}
			display="flex"
			justifyContent="space-between"
			alignItems="center"
		>
			<HStack>
				<Breadcrumb separator={<ChevronRightIcon />}>
					{ancestors.map((ancestor, idx) => (
						<BreadcrumbItem key={ancestor.id}>
							{!ancestor.parentFolderId && (
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
									{ancestor.parentFolderId
										? ancestor.folderName
										: rootFolderOwner.email}
								</Text>
							</BreadcrumbLink>
						</BreadcrumbItem>
					))}
				</Breadcrumb>
				{permissionType === "WRITE" && folder.parentFolderId && (
					<RenameFolderButton
						folder={folder}
						queriesToInvalidate={[
							["folder", folderId.toString(), "ancestors"],
						]}
					/>
				)}
			</HStack>
			<HStack>
				{
					<FolderInfoButton
						folder={folder}
						rootFolderOwner={rootFolderOwner}
						permissionType={permissionType}
					/>
				}
				{permissionType === "WRITE" && (
					<Menu>
						<MenuButton as={Button} rightIcon={<AddIcon />}>
							New
						</MenuButton>
						<MenuList>
							<NewFileButton folderId={folderId} />
							<NewFolderButton folderId={folderId} />
						</MenuList>
					</Menu>
				)}
			</HStack>
		</Box>
	);
};

export default Header;
