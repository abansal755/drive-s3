import { Fragment } from "react";
import { Heading, AlertIcon, AlertTitle } from "@chakra-ui/react";
import { Alert, VStack } from "../../common/framerMotionWrappers";
import PermissionRow from "./PermissionsList/PermissionRow";
import AddPermissionSelect from "./PermissionsList/AddPermissionSelect";
import Loading from "../../common/Loading";
import { useAuthContext } from "../../../context/AuthContext";
import { AnimatePresence } from "framer-motion";

const framerProps = {
	initial: { opacity: 0, scale: 0 },
	animate: { opacity: 1, scale: 1 },
	exit: { opacity: 0, scale: 0 },
};

const PermissionsList = ({
	rootFolderOwner,
	isUserOwner,
	resource,
	resourceType,
	permissionType,
	permissions,
	isLoading,
	isSuccess,
	isError,
}) => {
	const { user } = useAuthContext();

	return (
		<Fragment>
			<Heading size="md" mt={4} mb={4}>
				Permissions
			</Heading>
			{isError && (
				<Alert status="error" mb={2} {...framerProps}>
					<AlertIcon />
					<AlertTitle>Error fetching permissions</AlertTitle>
				</Alert>
			)}
			{isUserOwner && (
				<AddPermissionSelect
					resource={resource}
					resourceType={resourceType}
				/>
			)}
			<VStack alignItems="start" spacing={0} layout>
				<PermissionRow
					permission={{
						user: rootFolderOwner,
						permissionType: "OWNER",
					}}
					isUserOwner={isUserOwner}
				/>
				{!isUserOwner && (
					<PermissionRow
						permission={{
							user,
							permissionType,
						}}
						isUserOwner={isUserOwner}
					/>
				)}
				{isLoading && <Loading key="loading" />}
				<AnimatePresence>
					{isSuccess &&
						permissions.map((permission) => (
							<PermissionRow
								key={permission.id}
								permission={permission}
								isUserOwner={isUserOwner}
								resource={resource}
								resourceType={resourceType}
							/>
						))}
				</AnimatePresence>
			</VStack>
		</Fragment>
	);
};

export default PermissionsList;