import {
	CloseIcon,
	TriangleDownIcon,
	TriangleUpIcon,
	WarningTwoIcon,
} from "@chakra-ui/icons";
import {
	VStack,
	Text,
	Avatar,
	HStack,
	Badge,
	Tooltip,
	IconButton,
} from "@chakra-ui/react";
import { useTheme } from "@emotion/react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Fragment } from "react";
import { useAuthContext } from "../../../../context/AuthContext";
import { apiInstance } from "../../../../lib/axios";

const PermissionRow = ({ permission, isUserOwner, folder }) => {
	const { user } = useAuthContext();
	const theme = useTheme();
	const queryClient = useQueryClient();

	const deletePermissionMutation = useMutation({
		mutationFn: async () => {
			await apiInstance.delete(`/api/v1/permissions/${permission.id}`);
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ["folder", folder.id, "permissions"],
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
				queryKey: ["folder", folder.id, "permissions"],
			});
		},
	});

	return (
		<HStack
			key={permission.user.id}
			justifyContent="space-between"
			width="100%"
			sx={{
				":hover": {
					bgColor: theme.colors.blue[800],
				},
			}}
			px={3}
			py={2}
			borderRadius={3}
			transition="200ms"
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
							<Badge colorScheme="green">Read</Badge>
						)}
						{permission.permissionType === "WRITE" && (
							<Badge colorScheme="orange">Write</Badge>
						)}
						{permission.permissionType === "OWNER" && (
							<Badge colorScheme="red">Owner</Badge>
						)}
						{permission.user.id === user.id && (
							<Badge colorScheme="purple">You</Badge>
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
					{permission.permissionType === "READ" && (
						<Tooltip label="Upgrade permission to WRITE">
							<IconButton
								icon={<TriangleUpIcon />}
								isRound
								size="sm"
								colorScheme="purple"
								onClick={() =>
									modifyPermissionMutation.mutate("WRITE")
								}
							/>
						</Tooltip>
					)}
					{permission.permissionType === "WRITE" && (
						<Tooltip label="Downgrade permission to READ">
							<IconButton
								icon={<TriangleDownIcon />}
								isRound
								size="sm"
								colorScheme="purple"
								onClick={() =>
									modifyPermissionMutation.mutate("READ")
								}
							/>
						</Tooltip>
					)}
					<Tooltip label="Revoke Access">
						<IconButton
							icon={<CloseIcon />}
							colorScheme="red"
							isRound
							size="sm"
							onClick={deletePermissionMutation.mutate}
						/>
					</Tooltip>
				</HStack>
			)}
		</HStack>
	);
};

export default PermissionRow;
