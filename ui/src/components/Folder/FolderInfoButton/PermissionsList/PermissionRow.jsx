import { CloseIcon, WarningTwoIcon } from "@chakra-ui/icons";
import { VStack, Text, Avatar, Tooltip, IconButton } from "@chakra-ui/react";
import { HStack, Badge } from "../../../common/framerMotionWrappers";
import { useTheme } from "@emotion/react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuthContext } from "../../../../context/AuthContext";
import { apiInstance } from "../../../../lib/axios";
import ModifyPermissionButton from "./PermissionRow/ModifyPermissionButton";

const framerProps = {
	initial: { opacity: 0, scale: 0 },
	animate: { opacity: 1, scale: 1 },
	exit: { opacity: 0, scale: 0 },
};

const PermissionRow = ({ permission, isUserOwner, resource, resourceType }) => {
	const { user } = useAuthContext();
	const theme = useTheme();
	const queryClient = useQueryClient();

	const deletePermissionMutation = useMutation({
		mutationFn: async () => {
			await apiInstance.delete(`/api/v1/permissions/${permission.id}`);
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [
					resourceType === "FOLDER" ? "folder" : "file",
					resource.id,
					"permissions",
				],
			});
		},
	});

	const modifyPermissionMutation = useMutation({
		mutationFn: async (permissionType) => {
			await apiInstance.patch(`/api/v1/permissions/${permission.id}`, {
				permissionType,
			});
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: [
					resourceType === "FOLDER" ? "folder" : "file",
					resource.id,
					"permissions",
				],
			});
		},
	});

	return (
		<HStack
			key={permission.user.id}
			justifyContent="space-between"
			width="100%"
			px={3}
			py={2}
			borderRadius={3}
			whileHover={{ backgroundColor: theme.colors.blue[800] }}
			{...framerProps}
			layout
		>
			<HStack>
				<Avatar
					name={`${permission.user.firstName} ${permission.user.lastName}`}
					size="sm"
				/>
				<VStack spacing={0} alignItems="start">
					<HStack mb={-1}>
						<Text fontSize="md">{`${permission.user.firstName} ${permission.user.lastName}`}</Text>
						{permission.permissionType === "READ" && (
							<Badge colorScheme="green" {...framerProps}>
								Read
							</Badge>
						)}
						{permission.permissionType === "WRITE" && (
							<Badge colorScheme="orange" {...framerProps}>
								Write
							</Badge>
						)}
						{permission.permissionType === "OWNER" && (
							<Badge colorScheme="red" {...framerProps}>
								Owner
							</Badge>
						)}
						{permission.user.id === user.id && (
							<Badge colorScheme="purple" {...framerProps}>
								You
							</Badge>
						)}
					</HStack>
					<Text fontSize="sm">{permission.user.email}</Text>
				</VStack>
			</HStack>
			{permission.permissionType !== "OWNER" && isUserOwner && (
				<HStack>
					{permission.grantedToAnAncestorFolder && (
						<Tooltip label="Permission granted to an ancestor folder">
							<WarningTwoIcon
								boxSize={8}
								color={theme.colors.orange[300]}
							/>
						</Tooltip>
					)}
					<ModifyPermissionButton
						permissionType={permission.permissionType}
						modifyPermissionMutation={modifyPermissionMutation}
						deletePermissionMutation={deletePermissionMutation}
					/>
					<Tooltip label="Revoke Access">
						<IconButton
							icon={<CloseIcon />}
							colorScheme="red"
							isRound
							size="sm"
							onClick={deletePermissionMutation.mutate}
							isLoading={deletePermissionMutation.isPending}
							isDisabled={modifyPermissionMutation.isPending}
						/>
					</Tooltip>
				</HStack>
			)}
		</HStack>
	);
};

export default PermissionRow;
