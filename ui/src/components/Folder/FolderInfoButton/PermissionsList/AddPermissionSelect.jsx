import {
	Text,
	Avatar,
	Input,
	Button,
	AlertIcon,
	AlertTitle,
	InputGroup,
	InputRightElement,
} from "@chakra-ui/react";
import {
	VStack,
	Alert,
	HStack,
	ChevronDownIcon,
} from "../../../common/framerMotionWrappers";
import { useTheme } from "@emotion/react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useDebounce } from "@uidotdev/usehooks";
import { useState } from "react";
import { apiInstance, authInstance } from "../../../../lib/axios";
import { AnimatePresence } from "framer-motion";

const AddPermissionSelect = ({ resource, resourceType }) => {
	const [searchText, setSearchText] = useState("");
	const searchTextDebounced = useDebounce(searchText, 1000);
	const [isInputFocused, setIsInputFocused] = useState(false);
	const [isBtnHovered, setIsBtnHovered] = useState(false);
	const isSelectVisible = isInputFocused || isBtnHovered;

	const { data: users, isSuccess } = useQuery({
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
				resourceType,
				resourceId: resource.id,
				userId: user.id,
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

	const btnClickHandler = (permissionType, user) => {
		setIsBtnHovered(false);
		mutation.mutate({
			permissionType,
			user,
		});
	};

	const btnMouseEnterHandler = () => {
		if (isInputFocused) setIsBtnHovered(true);
	};

	return (
		<VStack display="block" position="relative" mb={3} layout>
			{mutation.isError && (
				<Alert
					status="error"
					mb={2}
					initial={{ opacity: 0, scale: 0 }}
					animate={{ opacity: 1, scale: 1 }}
					exit={{ opacity: 0, scale: 0 }}
				>
					<AlertIcon />
					<AlertTitle>
						{mutation.error.response.data.message}
					</AlertTitle>
				</Alert>
			)}
			<InputGroup>
				<Input
					value={searchText}
					onInput={(e) => setSearchText(e.target.value)}
					placeholder="Search for users to grant permissions"
					onFocus={() => setIsInputFocused(true)}
					onBlur={() => setIsInputFocused(false)}
				/>
				<InputRightElement>
					<HStack mr={12}>
						<Button size="xs" onClick={() => setSearchText("")}>
							Clear
						</Button>
						<ChevronDownIcon
							variants={{
								visible: {
									rotate: 180,
								},
								hidden: {
									rotate: 0,
								},
							}}
							animate={isSelectVisible ? "visible" : "hidden"}
						/>
					</HStack>
				</InputRightElement>
			</InputGroup>
			<AnimatePresence>
				{isSuccess && users.length > 0 && isSelectVisible && (
					<VStack
						bgColor={theme.colors.gray[800]}
						w="100%"
						borderRadius={4}
						p={3}
						position="absolute"
						zIndex={theme.zIndices.popover}
						mt={2}
						initial={{
							opacity: 0,
							y: "-20%",
						}}
						animate={{
							opacity: 1,
							y: 0,
						}}
						exit={{
							opacity: 0,
							scale: 0,
						}}
						maxH="300px"
						overflowY="auto"
					>
						{users.map((user) => (
							<HStack
								key={user.id}
								w="100%"
								whileHover={{
									backgroundColor: theme.colors.blue[800],
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
											btnClickHandler("READ", user)
										}
										size="sm"
										onMouseEnter={btnMouseEnterHandler}
										onMouseLeave={() =>
											setIsBtnHovered(false)
										}
									>
										Read
									</Button>
									<Button
										colorScheme="red"
										variant="ghost"
										onClick={() =>
											btnClickHandler("WRITE", user)
										}
										size="sm"
										onMouseEnter={btnMouseEnterHandler}
										onMouseLeave={() =>
											setIsBtnHovered(false)
										}
									>
										Write
									</Button>
								</HStack>
							</HStack>
						))}
					</VStack>
				)}
			</AnimatePresence>
		</VStack>
	);
};

export default AddPermissionSelect;
