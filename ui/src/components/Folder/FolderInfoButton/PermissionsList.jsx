import { Fragment } from "react";
import {
	Heading,
	VStack,
	Alert,
	AlertIcon,
	AlertTitle,
} from "@chakra-ui/react";
import PermissionRow from "./PermissionsList/PermissionRow";
import AddPermissionSelect from "./PermissionsList/AddPermissionSelect";
import Loading from "../../common/Loading";
import { useAuthContext } from "../../../context/AuthContext";

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
				<Alert status="error" mb={2}>
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
			<VStack alignItems="start" spacing={0}>
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
				{isLoading && <Loading />}
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
			</VStack>
		</Fragment>
	);
};

export default PermissionsList;
