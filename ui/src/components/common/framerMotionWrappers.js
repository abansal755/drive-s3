import { Button as ChakraButton, Text as ChakraText } from "@chakra-ui/react";
import {
	CheckCircleIcon as ChakraCheckCircleIcon,
	ExternalLinkIcon as ChakraExternalLinkIcon,
	CopyIcon as ChakraCopyIcon,
} from "@chakra-ui/icons";
import { motion } from "framer-motion";

export const Button = motion(ChakraButton);
export const Text = motion(ChakraText);
export const CheckCircleIcon = motion(ChakraCheckCircleIcon);
export const ExternalLinkIcon = motion(ChakraExternalLinkIcon);
export const CopyIcon = motion(ChakraCopyIcon);
