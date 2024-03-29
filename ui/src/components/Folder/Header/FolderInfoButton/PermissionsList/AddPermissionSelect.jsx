import {
	HStack,
	Text,
	Avatar,
	VStack,
	Input,
	Button,
	Alert,
	AlertIcon,
	AlertTitle,
} from "@chakra-ui/react";
import { useTheme } from "@emotion/react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useDebounce } from "@uidotdev/usehooks";
import { useState } from "react";
import { useAuthContext } from "../../../../../context/AuthContext";
import { apiInstance, authInstance } from "../../../../../lib/axios";

const AddPermissionSelect = ({ folder }) => {
	const [searchText, setSearchText] = useState("");
	const searchTextDebounced = useDebounce(searchText, 1000);
	const [isSelectVisible, setIsSelectVisible] = useState(false);
	const {
		data: users,
		isSuccess,
		isLoading,
	} = useQuery({
		queryKey: ["userSearch", searchTextDebounced],
		queryFn: async () => {
			const res = await authInstance.get(
				`/api/v1/users/search?v=${searchTextDebounced}`,
			);
			return res.data;
		},
		enabled: searchTextDebounced.length > 2,
	});
	const theme = useTheme();
	const queryClient = useQueryClient();

	const mutation = useMutation({
		mutationFn: async ({ permissionType, user }) => {
			await apiInstance.post("/api/v1/permissions", {
				permissionType,
				resourceType: "FOLDER",
				resourceId: folder.id,
				userId: user.id,
			});
		},
		onSuccess: () => {
			queryClient.invalidateQueries({
				queryKey: ["folder", folder.id, "permissions"],
			});
		},
	});

	return (
		<VStack display="block" position="relative" mb={3}>
			{mutation.isError && (
				<Alert status="error" mb={2}>
					<AlertIcon />
					<AlertTitle>
						{mutation.error.response.data.message}
					</AlertTitle>
				</Alert>
			)}
			<Input
				value={searchText}
				onInput={(e) => setSearchText(e.target.value)}
				placeholder="Search for users to grant permissions"
				onFocus={() => setIsSelectVisible(true)}
				onBlur={() => setIsSelectVisible(false)}
			/>
			{isSuccess && users.length > 0 && (
				<VStack
					bgColor={theme.colors.gray[800]}
					w="100%"
					borderRadius={4}
					p={3}
					position="absolute"
					zIndex={theme.zIndices.popover}
					mt={2}
					visibility={isSelectVisible ? "visible" : "hidden"}
					opacity={isSelectVisible ? 1 : 0}
					transition="200ms"
				>
					{users.map((user) => (
						<HStack
							key={user.id}
							w="100%"
							sx={{
								":hover": {
									bgColor: theme.colors.blue[800],
								},
							}}
							transition="200ms"
							p={2}
							borderRadius={3}
							justifyContent="space-between"
						>
							<HStack>
								<Avatar
									name={`${user.firstName} ${user.lastName}`}
									size="sm"
								/>
								<VStack spacing={0} alignItems="start">
									<Text
										fontSize="md"
										mb={-1}
									>{`${user.firstName} ${user.lastName}`}</Text>
									<Text fontSize="sm">{user.email}</Text>
								</VStack>
							</HStack>
							<HStack>
								<Button
									colorScheme="green"
									variant="ghost"
									onClick={() =>
										mutation.mutate({
											permissionType: "READ",
											user,
										})
									}
									size="sm"
								>
									Read
								</Button>
								<Button
									colorScheme="red"
									variant="ghost"
									onClick={() =>
										mutation.mutate({
											permissionType: "WRITE",
											user,
										})
									}
									size="sm"
								>
									Write
								</Button>
							</HStack>
						</HStack>
					))}
				</VStack>
			)}
		</VStack>
	);
};

export default AddPermissionSelect;
